int even(int n);

int odd(int n){
  if (n > 0) {
    if (n == 1) {
      return 1;
    }
    return even(n-1);
  }
  return 0;
}

int even(int n){
  if (n >= 0) {
    if (n == 0) {
      return 1;
    }
    return odd(n-1);
  }
  return 0;
}

int main(int n){
  print(even(n));
  return n;
}
