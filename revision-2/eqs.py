from cvc5.pythonic import *

s = Solver()

# 2-party addition in F7

s.resetAssertions()

m1, f11, x = FiniteFieldElems('m1 f11 x', 7)
m2, f21, y = FiniteFieldElems('m1 f21 y', 7)
p1, p2, o = FiniteFieldElems('p1 p2 o', 7)

# constraints from the protocol
s.add(m2 == x - f11)
s.add(m1 == y - f21)
s.add(p1 == m1 + f11)
s.add(p2 == m2 + f21)
s.add(o == p1 + p2)

# sanity check (should be sat)
s.check()

# correctness (should be unsat)
s.add(x + y != o)
s.check()


# 3-party addition in F7

s.resetAssertions()

m1, f11, x = FiniteFieldElems('m1 f11 x', 7)
m2, f21, y = FiniteFieldElems('m1 f21 y', 7)
p1, p2, o = FiniteFieldElems('p1 p2 o', 7)
