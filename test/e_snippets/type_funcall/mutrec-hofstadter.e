int m(int n);

int f(int n) {
  if (n >= 0){
    if (n == 0){
      return 1;
    } else {
      return n - m(f(n-1));
    }
  }
  return 0;
}

int m(int n) {
  if (n >= 0){
    if (n == 0){
      return 0;
    } else {
      return n - f(m(n-1));
    }
  }
  return 0;
}

int main(int n){
  int i = 0;
  while(i < n) {
    print(f(i));
    i = i + 1;
  }
  return 0;
}
