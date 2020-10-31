struct S {
  int t[4];
};

int main(int argc, char* argv[]){
  struct S s;
  (s.t)[0] = 1;
  print_int((s.t)[0]);
  return (s.t)[0];
}
