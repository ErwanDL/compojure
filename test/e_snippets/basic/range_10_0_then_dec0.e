main(n){
  if(n > 10) { n = 10; }
  else {
    if ( n < 0 ) { n = 0; }
    else { }
  }
  while( n > 0){
    n=n-1;
  }
  return n;
}
