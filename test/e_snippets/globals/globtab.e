
struct my_struct {
  int x;
  int y;
};

struct my_struct objs[2];

int main(){
  int i = 0;
  while (i < 2){
    (objs[i]).x = i;
    (objs[i]).y = i;
    i = i + 1;
  }
  return (objs[1]).x;
}
