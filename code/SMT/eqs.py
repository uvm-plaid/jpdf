## NOTE for chris: use /Users/ceskalka/csl/smt-tutorial/bin/activate, and make
## sure PYTHONPATH has been exported as in ~/.zsh

from cvc5.pythonic import *
import time

s = Solver()

# 2-party addition in F7

s.resetAssertions()

m1, f11, x = FiniteFieldElems('m1 f11 x', 2147483647)
m2, f21, y = FiniteFieldElems('m2 f21 y', 2147483647)
p1, p2, o = FiniteFieldElems('p1 p2 o', 2147483647)

# constraints from the protocol
s.add(m2 == x - f11)
s.add(m1 == y - f21)
s.add(p1 == m1 + f11)
s.add(p2 == m2 + f21)
s.add(o == p1 + p2)

# sanity check (should be sat)
s.check()

start_c1 = time.perf_counter()

# correctness (should be unsat)
s.check(x + y != o)  

stop_c1 = time.perf_counter()

# 3-party addition in F7

s.resetAssertions()

m21, m31, s1, rl1, r1 = FiniteFieldElems('m21 m31 s1 rl1 r1', 2147483647)
m12, m32, s2, rl2, r2 = FiniteFieldElems('m12 m32 s2 rl2 r2', 2147483647)
m13, m23, s3, rl3, r3 = FiniteFieldElems('m13 m23 s3 rl3 r3', 2147483647)
p1, p2, p3, o = FiniteFieldElems('p1 p2 p3 o', 2147483647)

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

start_c2 = time.perf_counter()

# correctness (should be unsat)

s.check(o != s1 + s2 + s3)

stop_c2 = time.perf_counter()

# BDOZ/SPDZ SHE scheme 

s.resetAssertions()

mac1,mac2,m1,m2,k1,k2,d,a = FiniteFieldElems('mac1 mac2 m1 m2 k1 k2 d a', 7)

s.add(mac1 == k1 + (d * m1))
s.add(mac2 == k2 + (d * m2))

start_c3 = time.perf_counter()

# HE for sum of shares,(should be unsat)
s.check(mac1 + mac2 != (k1 + k2) + (d * (m1 + m2)))

stop_c3 = time.perf_counter()

start_c4 = time.perf_counter()

# HE for multiplication of share and public value a (should be unsat)
s.check(mac1 * a != (k1 * a) + (d * (m1 * a)))

stop_c4 = time.perf_counter()


print(f"check 1 (2 party sum): { stop_c1 - start_c1:0.6f} seconds")

print(f"check 2 (3 party sum): { stop_c2 - start_c2:0.6f} seconds")

print(f"check 3 (BDOZ sum HE): { stop_c3 - start_c3:0.6f} seconds")

print(f"check 4 (BDOZ mult HE): { stop_c4 - start_c4:0.6f} seconds")

# andgmw gate in overture

s.resetAssertions()

mx1, mx2, mx21, my1, my2, my21, mz1, mz2 = FiniteFieldElems('mx1 mx2 mx21 my1 my2 my21 mz1 mz2', 2)
b1, b2, b3, b4, rz1 =  FiniteFieldElems('b1, b2, b3, b4, rz1', 2)

s.add(mx21 == mx2)
s.add(my21 == my2)
s.add(b1 == rz1 + ((mx1 + 0) * (my1 + 0)))
s.add(b2 == rz1 + ((mx1 + 0) * (my1 + 1)))
s.add(b3 == rz1 + ((mx1 + 1) * (my1 + 0)))
s.add(b4 == rz1 + ((mx1 + 1) * (my1 + 1)))
s.add(mz2 ==
       ((mx21 * my21) * b4) +
       ((mx21 * (my21 + 1)) * b3) +
       (((mx21 + 1) * my21) * b2) +
       (((mx21 + 1) * (my21 + 1)) * b1))
s.add(mz1 == rz1)

# This is the correctness property of the gate.
s.check(mz1 + mz2 != (mx1 + mx2) * (my1 + my2))

s.resetAssertions()

## This version more directly maps to prelude version

mx1, mx2, mx21, my1, my2, my21, mz1, mz2, rz1 = FiniteFieldElems('mx1 mx2 mx21 my1 my2 my21 mz1 mz2 rz1', 2)

s.add(mx21 == mx2)
s.add(my21 == my2)
s.add(mz2 ==
       ((mx21 * my21) * rz1 + ((mx1 + 1) * (my1 + 1))) +
       ((mx21 * (my21 + 1)) * rz1 + ((mx1 + 1) * (my1 + 0))) +
       (((mx21 + 1) * my21) * rz1 + ((mx1 + 0) * (my1 + 1))) +
       (((mx21 + 1) * (my21 + 1)) * rz1 + ((mx1 + 0) * (my1 + 0))))
s.add(mz1 == rz1)

# This is the correctness property of the gate.
s.check(mz1 + mz2 != (mx1 + mx2) * (my1 + my2))
