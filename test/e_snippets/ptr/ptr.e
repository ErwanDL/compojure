void f(int* z, int x, int y){
  *z = x + y;
}
int main(int x, int y){
  int z = 3;
  f(&z, x, y);
  return z;
}
