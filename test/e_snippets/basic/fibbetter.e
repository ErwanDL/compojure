main(n){
  i = 0; // t6
  j = 1; // t5
  k = 0; // t4
  while(k < n){
    tmp = i + j;
    i = j;
    j = tmp;
    k = k + 1;
  }
  return i;
}
