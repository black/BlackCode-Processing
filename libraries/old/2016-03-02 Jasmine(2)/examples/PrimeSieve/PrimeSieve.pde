/**
Jasmine Library Example

Prime number sieve

This example demonstrates the creation and evaluation of 
an algorithm. The is used to decide whether an integer 
is an odd prime number.

The odd prime numbers up to 40000 are show as bright dots 
in the display.

created by Peter Lager 2014
*/

import org.quark.jasmine.*;

String[] lines = {
  "# Test to see if n is an odd prime number", 
  "root = floor(sqrt(n)); notPrime = FALSE; p = 3", 
  "while(p <= root && notPrime == FALSE)", 
  "  if(n % p == 0); notPrime = TRUE; endif", 
  "  p = p + 1", 
  "wend"
};

Algorithm a;

void setup() {
  size(400, 400);
  Compile.init();
  background(0, 0, 64);
  fill(0, 0, 255);
  noStroke();
  Algorithm a = Compile.algorithm(lines, false);

  for (int i = 3; i < 40000; i += 2) {
    a.eval("n", i);
    if (!a.answer("notPrime").toBoolean()) {
      int x = (i % 200) *2;
      int y = (i / 200) * 2;
      rect(x, y, 2, 2);
    }
  }
}
