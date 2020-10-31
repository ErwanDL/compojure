int main(){
  int x = 0;
  int *y = &x;
  int** z = &y;
  **z = 3;
  return x;
}
