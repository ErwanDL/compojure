main(){
  i = 10;
  a = 2;
  res = 0;
  while(i > 0){
    x = a + a;
    y = 1 + x;
    if(i > 4){
      res = res + x;
    } else {
      if (i < 2){
        res = res - y;
      } else {
        res = res + x - y;
      }
    }
    i = i - 1;
  }
  return res;
}
