int main(int n){
  int i = 0; // t6
  int j = 1; // t5
  int k = 0; // t4
  while(k < n){
    int tmp = i + j;
    i = j;
    j = tmp;
    k = k + 1;
  }
  return i;
}
