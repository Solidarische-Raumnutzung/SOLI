#!/bin/bash
set -eux
data="$(git log "$@" --pretty=format:%ad --date=format:"%w %H" |
    awk '
        $1 == 0 { $1 = 7 }
        { h[$1 " " $2]++ }
        END {
            for (i in h)
                if (max < h[i])
                    max = h[i]
            # scale the values into the range [0..7)
            max = (max - 1) / 7
            for (i in h)
                print i " " h[i] / max
        }
    ')"

gnuplot <<EOF
unset key
set title "Commits pro Stunde und Wochentag"
set xlabel "Wochentag"
set xrange [0:8]
set xtics ("" 0, "Mon" 1, "Die" 2, "Mit" 3, "Don" 4, "Fre" 5, "Sam" 6, "Son" 7)
set ylabel "Stunde"
set ytics 6

set terminal pngcairo
set output "commits_per_hour.png"

plot '-' using 1:2:3 with points pt 6 ps variable lc 'web-blue'
$data
e
EOF


data="$(git log --pretty=format:"%ad" --date=short | sort | uniq -c |
    awk '
        BEGIN {count=0;} {count+=$1; if (NR%7==0) {print count; count=0;}}
    ')"

gnuplot <<EOF
unset key
set title "Commits pro Woche"

set terminal pngcairo
set output "commits_per_week.png"
plot '-' with boxes
$data
e
EOF



# This is specific to SOLI, remove it if you use this elsewhere
mv commits_per_hour.png docs/implementationsbericht/figures/hours.png
mv commits_per_week.png docs/implementationsbericht/figures/weeks.png