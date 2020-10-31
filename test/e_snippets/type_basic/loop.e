int main(){
  int i = 10;
  int a = 2;
  int res = 0;
  while(i > 0){
    int x = a + a;
    if(a > 4){
      res = res + x;
    } else {
      res = res - x;
    }
    i = i - 1;
  }
  return res;
}
