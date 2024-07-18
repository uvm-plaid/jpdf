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
s.check(x + y != o)  

# 3-party addition in F7

s.resetAssertions()

m21, m31, s1, rl1, r1 = FiniteFieldElems('m21 m31 s1 rl1 r1', 7)
m12, m32, s2, rl2, r2 = FiniteFieldElems('m12 m32 s2 rl2 r2', 7)
m13, m23, s3, rl3, r3 = FiniteFieldElems('m13 m23 s3 rl3 r3', 7)
p1, p2, p3, o = FiniteFieldElems('p1 p2 p3 o', 7)

s.add(m12 == s1 - rl1 - r1)
s.add(m13 == r1)
s.add(m21 == s2 - rl2 - r2)
s.add(m23 == r2)
s.add(m31 == s3 - rl3 - r3)
s.add(m32 == r3)
s.add(p1 == rl1 + m21 + m31)
s.add(p2 == rl2 + m12 + m32)
s.add(p3 == rl3 + m13 + m23)
s.add(o == p1 + p2 + p3)

# sanity check (should be sat)
s.check()

# correctness (should be unsat)
s.check(o != s1 + s2 + s3)

# BDOZ/SPDZ SHE scheme 

s.resetAssertions()

mac1,mac2,m1,m2,k1,k2,d,a = FiniteFieldElems('mac1 mac2 m1 m2 k1 k2 d a', 7)

s.add(mac1 == k1 + (d * m1))
s.add(mac2 == k2 + (d * m2))

# HE for sum of shares,(should be unsat)
s.check(mac1 + mac2 != (k1 + k2) + (d * (m1 + m2)))

# HE for multiplication of share and public value a (should be unsat)
s.check(mac1 * a != (k1 * a) + (d * (m1 * a)))

