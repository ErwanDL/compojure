int main(){
  int i = 20;
  int c = 8;
  int a = 5;
  int b = 0;
  while(i > 0){
    if(i < 5){
      a = 4 + c;
    }
    b = b + a;
    i = i - 1;
  }
  return b;
}
