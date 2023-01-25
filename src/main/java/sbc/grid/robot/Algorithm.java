package sbc.grid.robot;

public interface Algorithm<R, A> {
    R compute(A arg);
}
