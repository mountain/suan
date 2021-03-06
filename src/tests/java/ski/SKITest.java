package ski;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ski.SKI.*;

class SKITest {

    @Test
    void testI() {
        Combinator fml = cons(I(), var("x"));
        assertEquals("(I, x)", fml.script());
        assertEquals("x", fml.eval().script());
    }

    @Test
    void testK() {
        Combinator fml = cons(cons(K(), var("x")), var("y")) ;
        assertEquals("((K, x), y)", fml.script());
        assertEquals("x", fml.eval().script());
    }

    @Test
    void testS() {
        Combinator fml = cons(cons(cons(S(), var("x")), var("y")), var("z"));
        assertEquals("(((S, x), y), z)", fml.script());
        assertEquals("((x, z), (y, z))", fml.eval().script());
    }

    @Test
    void testFalse() {
        Combinator fml = cons(cons(cons(S(), K()), var("x")), var("y"));
        assertEquals("(((S, K), x), y)", fml.script());
        assertEquals("y", fml.eval().script());
    }

    @Test
    void testReverse() {
        Combinator reverse = cons(cons(S(), cons(K(), cons(S(), I()))), K());
        Combinator fml = cons(cons(reverse, var("x")), var("y"));
        assertEquals("((((S, (K, (S, I))), K), x), y)", fml.script());
        assertEquals("(y, x)", fml.eval().script());
    }

    @Test
    void testKSKS() {
        Combinator fml = cons(cons(cons(K(), S()), K()), S());
        assertEquals("(((K, S), K), S)", fml.script());
        assertEquals("(S, S)", fml.eval().script());
    }

    @Test
    void testKKKSKS() {
        Combinator fml = cons(cons(cons(cons(cons(K(), K()), K()), S()), K()), S());
        assertEquals("(((((K, K), K), S), K), S)", fml.script());
        assertEquals("(S, S)", fml.eval().script());
    }

    @Test
    void testSKK() {
        Combinator skk = cons(cons(S(), K()), K());
        Combinator fml = cons(cons(skk, var("x")), var("y"));
        assertEquals("((S, K), K)", skk.script());
        assertEquals("(x, y)", fml.eval().script());
    }

    @Test
    void testII() {
        Combinator ii = cons(I(), I());
        assertEquals("(I, I)", ii.script());
        assertEquals("I", ii.eval().script());
    }

    @Test
    void testIII() {
        Combinator iii = cons(cons(I(), I()), I());
        assertEquals("((I, I), I)", iii.script());
        assertEquals("I", iii.eval().script());
    }

    @Test
    void testIIII() {
        Combinator iiii = cons(cons(I(), I()), cons(I(), I()));
        assertEquals("((I, I), (I, I))", iiii.script());
        assertEquals("I", iiii.eval().script());
    }

    @Test
    void testIIIII() {
        Combinator iiiii = cons(cons(cons(I(), I()), cons(I(), I())), I());
        assertEquals("(((I, I), (I, I)), I)", iiiii.script());
        assertEquals("I", iiiii.eval().script());
    }

    @Test
    void testIIIIII() {
        Combinator iiiiii = cons(cons(cons(I(), I()), cons(I(), I())), cons(I(), I()));
        assertEquals("(((I, I), (I, I)), (I, I))", iiiiii.script());
        assertEquals("I", iiiiii.eval().script());
    }

    @Test
    void testReplicate1() {
        Combinator replicator = cons(cons(cons(S(), I()), I()), cons(cons(S(), I()), I()));
        assertEquals("(((S, I), I), ((S, I), I))", replicator.script());
        assertEquals("(((S, I), I), ((S, I), I))", replicator.eval().script());
    }

    @Test
    void testReplicate2() {
        Combinator replicator = cons(cons(S(), cons(K(), var("x"))), cons(cons(S(), I()), I()));
        Combinator fml = cons(replicator, var("y"));
        assertEquals("((S, (K, x)), ((S, I), I))", replicator.script());
        assertEquals("(x, (y, y))", fml.eval().script());
    }

    @Test
    void testReplicate3() {
        Combinator replicator = cons(cons(S(), cons(K(), I())), cons(cons(S(), I()), I()));
        Combinator fml = cons(replicator, cons(cons(S(), I()), I()));
        assertEquals("((S, (K, I)), ((S, I), I))", replicator.script());
        assertEquals("(((S, I), I), ((S, I), I))", fml.eval().script());
    }

    @Test
    void testSelfApplication() {
        Combinator replicator = cons(cons(S(), cons(K(), I())), cons(cons(S(), I()), I()));
        Combinator selfappl = cons(replicator, replicator);
        assertEquals("((S, (K, I)), ((S, I), I))", replicator.script());
        assertEquals("(((S, I), I), ((S, (K, I)), ((S, I), I)))", selfappl.eval().script());
    }

}

