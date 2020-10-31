int g(int a,int b){
  print(a);
  print(b);
  return (b - a);
}
int f(int a, int b){
  print(a);
  print(b);
  return g(b, a);
}
int main(int x, int y){
  return f(x, y);
}
