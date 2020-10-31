int main(){
  int i = 0;
  int x;
  while(i < 10){
    if(i == 0){
      x = 1;
    }
    x = x + i;
    i = i + 1;
  }
  return x;
}

