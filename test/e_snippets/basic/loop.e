main(){
  i = 10;
  a = 2;
  res = 0;
  while(i > 0){
    x = a + a;
    if(a > 4){
      res = res + x;
    } else {
      res = res - x;
    }
    i = i - 1;
  }
  return res;
}
