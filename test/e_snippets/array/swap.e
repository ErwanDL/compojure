int swap(int* t,int i,int j){
  int tmp = t[i];
  t[i] = t[j];
  t[j] = tmp;
  return 0;
}
int main(){
  int t[10];
  t[0] = 5;
  t[1] = 12;
  swap(t,0,1);
  print(t[0]);
  print(t[1]);
  return 0;
}
