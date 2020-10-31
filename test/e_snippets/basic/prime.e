main(n){
  print(n);
  while(n%2 == 0){
    print(2);
    n = n / 2;
  }
  f = 3;
  while ( f * f <= n ){
    if ( n % f == 0 ) {
      print(f);
      n = n / f;
    } else {
      f = f + 2;
    }
  }
  if ( n != 1 ) {
    print(n);
  }
  return 0;
}
