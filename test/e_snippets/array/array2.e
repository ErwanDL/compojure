int main(){
  int t[10];
  int i = 0;
  while(i < 10){
    t[i] = i;
    i = i + 1;
  }
  int sum = 0;
  i = 0;
  while(i < 10){
    sum = sum + t[i];
    i = i + 1;
  }
  return sum;
}
