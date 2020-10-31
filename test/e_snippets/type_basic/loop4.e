int main(){
  int i = 10;
  int a = 2;
  int res = 0;
  while(i > 0){
    int x = a + a;
    int y = 1 + x;
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
