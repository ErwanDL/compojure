int swap(int* t ,int i,int j){

  int tmp = t[i];
  t[i] = t[j];
  t[j] = tmp;
  return 0;
}
int partition(int* t, int begin, int end, int pivot){
  swap(t, pivot, end);
  int j = begin;
  int i = begin;
  while(i <= end - 1){
    if(t[i] <= t[end]){
      swap(t, i, j);
      j = j + 1;
    }
    i = i + 1;
  }
  swap(t, end, j);
  return j;
}
int sort(int* t, int begin, int end){
  if (begin < end){
    int pivot = begin;
    pivot = partition(t, begin, end, pivot);
    sort(t, begin, pivot-1);
    sort(t, pivot+1, end);
  }
}
int main(){
  int t[10];
  t[0] = 5;
  t[1] = 7;
  t[2] = 3;
  t[3] = 8;
  t[4] = 11;
  t[5] = 1;
  t[6] = 4;
  t[7] = 7;
  t[8] = 2;
  t[9] = 9;
  sort(t,0,9);
  int i = 0;
  while(i < 10){
    print(t[i]);
    i = i + 1;
  }
  return 0;
}
