
int max = 20;
int objs[20];

int main(){
  int i = 0;
  int sum = 0;
  while (i < max){
    objs[i] = sum;
    sum = sum + i;
    i = i + 1;
  }
  i = 0;
  while (i < max){
    print(objs[i]);
    i = i + 1;
  }
  return objs[max-1];
}
