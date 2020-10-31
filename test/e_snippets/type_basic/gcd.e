int main(int a, int b){
  int t;
  while(b != 0){
    t = b;
    b = a % b;
    a = t;
  }
  return a;
}
