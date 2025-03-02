package edu.kit.hci.soli.test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public sealed interface Either<L, R> {
    default L assertLeft() {
        return (L) assertInstanceOf(Left.class, this).value;
    }
    default R assertRight() {
        return (R) assertInstanceOf(Right.class, this).value;
    }

    record Left<L, R>(L value) implements Either<L, R> { }
    record Right<L, R>(R value) implements Either<L, R> { }
}
