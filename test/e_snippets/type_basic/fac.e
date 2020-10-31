int main(int n){
  if(n < 0) { n = 0; }
  else { }

  int res = 1;
  while(n > 0){
    res = n * res;
    n = n - 1;
  }

  return res;
}
