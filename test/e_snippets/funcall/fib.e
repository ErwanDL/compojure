fib(n){
  if(n > 14){ return -1; }
  if(n <= 2){
    return 1;
  } else {
    return fib(n-1) + fib(n-2);
  }
}
main(n){
  return fib(n);
}
