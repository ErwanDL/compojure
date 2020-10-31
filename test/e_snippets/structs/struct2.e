struct mastruct {
  int x;
  int y;
};

int main(){
  struct mastruct S;
  struct mastruct T;
  S.x = 12;
  S.y = 3;
  T.x = 14;
  T.y = 15;
  return (S.x + S.y);
}
