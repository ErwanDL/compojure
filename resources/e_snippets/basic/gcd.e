main(a,b){
  while(b != 0){
    t = b;
    b = a % b;
    a = t;
  }
  return a;
}
