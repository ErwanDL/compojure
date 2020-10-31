main(){
  i = 20;
  c = 8;
  a = 5;
  b = 0;
  while(i > 0){
    if(i < 5){
      a = 4 + c;
    }
    b = b + a;
    i = i - 1;
  }
  return b;
}
