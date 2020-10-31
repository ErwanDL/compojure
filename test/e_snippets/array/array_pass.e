int f(int* a, int i){
  a[1] = i;
  return 0;
}
int main(){
  int t[3];
  int x = f(t,5);
  return t[1];
}
