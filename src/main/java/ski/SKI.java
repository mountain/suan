package ski;

import java.util.HashMap;
import java.util.HashSet;

public interface SKI {

    class Detector extends HashMap<Thread, HashSet<String>> {
        public boolean commit(String script) {
            Thread cur = Thread.currentThread();
            if (!this.containsKey(cur)) {
                this.put(cur, new HashSet<>());
            }
            return this.get(cur).add(script);
        }
    }

    Detector detector = new Detector();

    interface Combinator {
        String script();
        Context tokenize(int maxdepth);
        Combinator eval();
    }

    class Context extends HashMap<String, Combinator> {
        int counter;
        int maxdepth;
        StringBuffer snippet;

        Context(int maxdepth) {
            this.counter = 1;
            this.snippet = new StringBuffer();
            this.maxdepth = maxdepth;
        }

        public String script() {
            return this.snippet.toString();
        }

        public void visitLeaf(Combinator leaf) {
            this.snippet.append(leaf.script());
        }

        public void visitLeft(Combinator left, int curdepth) {
            this.snippet.append("(");
            if (left instanceof CompositiveCombinator comp) {
                if (curdepth <= this.maxdepth) {
                    this.visitLeft(comp.left, curdepth + 1);
                    this.visitRoot();
                    this.visitRight(comp.right);
                } else {
                    String key = String.format("$%d", this.counter);
                    this.snippet.append(key);
                    this.put(key, left);
                    this.counter++;
                }
            } else {
                this.snippet.append(left.script());
            }
        }

        public void visitRoot() {
            this.snippet.append(",");
            this.snippet.append(" ");
        }

        public void visitRight(Combinator right) {
            String key = String.format("$%d", this.counter);
            this.snippet.append(key);
            this.snippet.append(")");
            this.put(key, right);
            this.counter++;
        }
    }

    class CompositiveCombinator implements Combinator {
        public final Combinator left;
        public final Combinator right;

        CompositiveCombinator(Combinator left, Combinator right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public String script() {
            return String.format("(%s, %s)", left.script(), right.script());
        }

        @Override
        public Context tokenize(int maxdepth) {
            Context ctx = new Context(maxdepth);
            ctx.visitLeft(left, 1);
            ctx.visitRoot();
            ctx.visitRight(right);
            return ctx;
        }

        @Override
        public Combinator eval() {
            Context ctx = this.tokenize(0);
            String script0 = ctx.script();
            return switch(script0) {
                case "(I, $1)" -> ctx.get("$1");
                default  -> {
                    ctx = this.tokenize(1);
                    String script1 = ctx.script();
                    yield switch (script1) {
                        case "((I, $1), $2)" -> {
                            Combinator $1 = ctx.get("$1");
                            Combinator $2 = ctx.get("$2");
                            if(detector.commit(this.script())) {
                                yield cons($1.eval(), $2.eval()).eval();
                            } else {
                                yield cons($1.eval(), $2.eval());
                            }
                        }
                        case "((K, $1), $2)" -> ctx.get("$1");
                        default -> {
                            ctx = this.tokenize(2);
                            String script2 = ctx.script();
                            yield switch (script2) {
                                case "(((I, $1), $2), $3)" -> {
                                    Combinator $1 = ctx.get("$1");
                                    Combinator $2 = ctx.get("$2");
                                    Combinator $3 = ctx.get("$3");
                                    if(detector.commit(this.script())) {
                                        yield cons(cons($1.eval(), $2.eval()).eval(), $3.eval()).eval();
                                    } else {
                                        yield cons(cons($1.eval(), $2), $3);
                                    }
                                }
                                case "(((K, $1), $2), $3)" -> {
                                    Combinator $1 = ctx.get("$1");
                                    Combinator $3 = ctx.get("$3");
                                    yield cons($1, $3).eval();
                                }
                                case "(((S, $1), $2), $3)" -> {
                                    Combinator $1 = ctx.get("$1");
                                    Combinator $2 = ctx.get("$2");
                                    Combinator $3 = ctx.get("$3");
                                    CompositiveCombinator result;
                                    if(detector.commit(this.script())) {
                                        result = (CompositiveCombinator)cons(cons($1, $3).eval(), cons($2, $3).eval());
                                    } else {
                                        result = (CompositiveCombinator)cons(cons($1, $3), cons($2, $3));
                                    }
                                    yield result.eval();
                                }
                                case "(($1, $2), ($3, $4))" -> {
                                    Combinator $1 = ctx.get("$1");
                                    Combinator $2 = ctx.get("$2");
                                    Combinator $3 = ctx.get("$3");
                                    Combinator $4 = ctx.get("$4");
                                    CompositiveCombinator result = (CompositiveCombinator)cons(cons($1, $2).eval(), cons($3, $4).eval()).eval();
                                    yield result;
                                }
                                case "((($1, $2), $3), $4)" -> {
                                    Combinator $1 = ctx.get("$1").eval();
                                    Combinator $2 = ctx.get("$2");
                                    Combinator $3 = ctx.get("$3");
                                    Combinator $4 = ctx.get("$4");
                                    if(detector.commit(this.script())) {
                                        yield cons(cons(cons($1, $2), $3).eval(), $4).eval();
                                    } else {
                                        yield cons(cons(cons($1, $2), $3).eval(), $4.eval());
                                    }
                                }
                                default -> this;
                            };
                        }
                    };
                }
            };
        }
    }

    static Combinator cons(Combinator a, Combinator b) {
        return new CompositiveCombinator(a, b);
    }

    static Combinator var(String name) {
        return new Combinator() {

            @Override
            public String script() {
                return name;
            }

            @Override
            public Context tokenize(int maxdepth) {
                return null;
            }

            @Override
            public Combinator eval() {
                return this;
            }

        };
    }

    static Combinator S() {
        return new Combinator() {

            @Override
            public String script() {
                return "S";
            }

            @Override
            public Context tokenize(int maxdepth) {
                return null;
            }

            @Override
            public Combinator eval() {
                return this;
            }

        };
    }

    static Combinator K() {
        return new Combinator() {

            @Override
            public String script() {
                return "K";
            }

            @Override
            public Context tokenize(int maxdepth) {
                return null;
            }

            @Override
            public Combinator eval() {
                return this;
            }

        };
    }

    static Combinator I() {
        return new Combinator() {

            @Override
            public String script() {
                return "I";
            }

            @Override
            public Context tokenize(int maxdepth) {
                return null;
            }

            @Override
            public Combinator eval() {
                return this;
            }

        };
    }

}
