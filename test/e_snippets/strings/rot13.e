int and(int a, int b){
  if(a != 0){ return b; }
  return 0;
}

int rot13c(char x){
  if('A' <= x){
    if (x <= 'Z'){
      return 'A' + (x - 'A' + 13) % 26;
    }
  }
  if('a' <= x){
    if (x <= 'z'){
      return 'a' + (x - 'a' + 13) % 26;
    }
  }
  return x;
}

int rot13(char* c){
  int i = 0;

  while(c[i] != 0){
    c[i] = rot13c(c[i]);
    i = i + 1;
  }
  return 0;
}
int main(int argc,char* argv[]){
  int a = atoi(argv[1]);
  int b = atoi(argv[2]);
  char c[10];
  c[0] = 'C';
  c[1] = 'o';
  c[2] = 'u';
  c[3] = 'c';
  c[4] = 'o';
  c[5] = 'u';
  c[6] = 0;

  rot13(c);
  print_string(c);
  return 0;
}
