int fib(int n){
  if(n > 14){ return -1; }
  if(n <= 2){
    return 1;
  } else {
    return fib(n-1) + fib(n-2);
  }
}
int main(int n){
  return fib(n);
}
