int main(int argc,char* argv[]){
  char* c = "Coucou\n";
  char* d = "Hello!\n";
  char* e[2];
  e[0] = c;
  e[1] = d;
  print_string(e[0]);
  print_string(e[1]);
  return 0;
}
