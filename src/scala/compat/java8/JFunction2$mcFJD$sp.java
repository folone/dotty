
/*
 * Copyright (C) 2012-2014 Typesafe Inc. <http://www.typesafe.com>
 */

package scala.compat.java8;

@FunctionalInterface
public interface JFunction2$mcFJD$sp extends JFunction2 {
    abstract float apply$mcFJD$sp(long v1, double v2);

    default Object apply(Object v1, Object v2) { return (Float) apply$mcFJD$sp((Long) v1, (Double) v2); }
}
