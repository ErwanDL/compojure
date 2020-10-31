int main(int argc, char* argv[]){
  print_string("Program launched with ");
  print_int(argc);
  print_string(" arguments\n");
  int i = 0;
  while(i < argc){
    print_string("argv[");
    print_int(i);
    print_string("] = ");
    print_string(argv[i]);
    print_string("\n");
    i = i + 1;
  }
  return argc;
}
