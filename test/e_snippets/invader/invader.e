int push_button_get();
void timer_set_and_wait(int period, int time);
void clear_screen(int color);
int read_pixel(int x, int y, int scale);
void write_pixel_scaling(int pixel, int x, int y, int scale);
void set_bg_color(int color);
void set_fg_color(int color);
void set_display_cur_pos(int x, int y);
void display_string(char *msg);
void set_display_scale(int s);
int scale = 4;
int TIMER_FREQ = 10000000;

void dirty_exit(){
  int x;
  *(&x+1000000000) = 42;
}

/* Etats */

struct etat {
	int dx;
	int dy;
	int etat_suivant;
};

struct etat etats[5];

void init_etat(int index, int dx, int dy, int next){
  (etats[index]).dx = dx;
  (etats[index]).dy = dy;
  (etats[index]).etat_suivant = next;
}

/* Objets */

struct Object {
	int alive;
	int period;
	int deadline;
	int x;
  int y;
	int dx;
  int dy;
	char *pattern;
	int color;
	int bg[64]; /* background */
	int ax;
  int ay;
};

struct Object object[7];

int NOBJ = 7;

void init_object(int index, int alive, int period, int deadline,
                 int x, int y, int dx, int dy, char* pattern, int color){
  (object[index]).alive = alive;
  (object[index]).period = period;
  (object[index]).deadline = deadline;
  (object[index]).x = x;
  (object[index]).y = y;
  (object[index]).dx = dx;
  (object[index]).dy = dy;
  (object[index]).pattern = pattern;
  (object[index]).color = color;
}

/* Sprites */

char sprite_sship[8];
char sprite_laser[8];
char sprite_alien1[8];
char sprite_alien2[8];
char sprite_alien3[8];
char sprite_alien4[8];
char sprite_alien5[8];

void init_sprite(char* t,  char a0, char a1, char a2, char a3,
                 char a4, char a5, char a6, char a7){
  t[0] = a0;
  t[1] = a1;
  t[2] = a2;
  t[3] = a3;
  t[4] = a4;
  t[5] = a5;
  t[6] = a6;
  t[7] = a7;
}

void initialize()
{
	int i;
  int dx;
  int dy;
	clear_screen(0x333333);
  i = 0;
	while(i < NOBJ) {
		if (i == 1) {
			/* laser */
			(object[i]).alive = 0;
			(object[i]).period = 1;
		} else {
			/* spaceship or aliens */
			(object[i]).alive = 1;
			if (i == 0){
				/* spaceship */
				(object[i]).period = 3;
      }
			else{
				/* aliens */
				(object[i]).period = 4;
      }
		}
		(object[i]).deadline = 1;
		if (i > 1) {
			/* aliens */
			if (i > 4) {
				/* alien4 or alien5 */
				(object[i]).y = 3; /* 3rd line */
				(object[i]).x = 6 + (i - 4) * 8 ;
			} else {

				/* alien1, alien2 or alien3 */
				(object[i]).y = 1; /* 1st line */
				(object[i]).x = 10 + (i - 2) * 8;
			}
			(object[i]).dx = -1;
			(object[i]).dy = 0;
		}
		(object[i]).ax = -1;
		(object[i]).ay = -1;

		/* initialization of object background considering the last one */
    int* ptr = (object[i]).bg;
    dx = 0;
		while (dx < 8){
      dy = 0;
			while (dy < 8){
        int p = read_pixel((((object[i]).x) * 8) + dx,
                           (((object[i]).y) * 8) + dy, scale);
				ptr[dx*8+dy] = p;
        dy = dy + 1;
      }
      dx = dx + 1;
    }
    i = i + 1;
	}
}

/* function to display the 8 pixels of a pattern line */
void display_pattern_line(int m, int x, int y, int color)
{
	int i = 0;

	while (i < 8){
    if ((m & 1) == 1){
      write_pixel_scaling(color, x + i, y, scale);
    }
		m = m / 2;
    i = i + 1;
	}
}

/* function to display an 8x8 object considering the last background */
void display_pattern(char* pattern, int x, int y, int color)
{
	int i = 0;

  while(i < 8){
		display_pattern_line(pattern[i], x, y + i, color);
    i = i + 1;
  }
}

/* function to display an 8x8 object (spaceship, laser or alien) */
void display_sprite(struct Object *object)
{
	int dx; int dy;
	if ((object->ax > -1 && object->ay > -1) &&
      (object->x != object->ax || object->y != object->ay || !(*object).alive))
    {
      int* ptr = object->bg;
      dx = 0;
      while(dx < 8) {
        dy = 0;
        while(dy < 8) {
          write_pixel_scaling(ptr[dx*8+dy],
                              ((object->ax) *8) + dx, ((object->ay) *8 ) + dy, scale);
          if (!object->alive){
            ptr[dx*8 + dy] = read_pixel(((object->x) *8) + dx,
                                        ((object->y) *8) + dy, scale);
          }
          dy = dy + 1;
        }
        dx = dx + 1;
      }
    }
	object->ax = object->x;
	object->ay = object->y;
	if ((*object).alive){
		display_pattern(object->pattern, (object->x) * 8, (object->y) * 8,
		                object->color);
  }
}

int main(int argc, char* argv)
{
	/* declaration of local variables */
	int i;
  int dx;
  int dy;
	int push_state;
  int alien_state;
  int edge_reached;
	int n_aliens;
  struct Object *spaceship;
  struct Object *laser;

  init_etat(0, 0, 1, 1);
  init_etat(1, 0, 1, 2);
  init_etat(2, 1, 0, 3);
  init_etat(3, 0, 1, 4);
  init_etat(4, -1, 0, 1);

  init_sprite(sprite_sship,  0x00, 0x3c, 0x7e, 0xff, 0xff, 0xe7, 0xc3, 0xc3);
  init_sprite(sprite_laser,  231, 231, 255, 255, 126, 60, 24, 24);
  init_sprite(sprite_alien1, 0xc3, 0x3c, 0x5a, 0xff, 0xff, 0x81, 0x42, 0x24);
  init_sprite(sprite_alien2, 0xc3, 0x3c, 0x5a, 0xff, 0xff, 0xa5, 0xa5, 0x5a);
  init_sprite(sprite_alien3, 0x42, 0x24, 0x3c, 0x5a, 0xff, 0xbd, 0x81, 0x42);
  init_sprite(sprite_alien4, 0x81, 0x42, 0x3c, 0x5a, 0x5a, 0x3c, 0x42, 0x81);
  init_sprite(sprite_alien5, 0x41, 0x22, 0x3e, 0x6b, 0x49, 0x7f, 0x3e, 0x55);
  /*
    0x41  .#....#.
    0x22  ..#...#.
    0x3e  ..#####.
    0x6b  .##.#.##
    0x49  .#..#..#
    0x7f  .#######
    0x3e  ..#####.
    0x55  .#.#.#.#
   */
  init_object(0, 1, 3, 1, 18, 32, 0, 0, sprite_sship,  0x0000FF); /* blue spaceship */
  init_object(1, 0, 1, 1, 18, 0,  0, 0, sprite_laser,  0xffc0cb); /* white laser */
  init_object(2, 1, 4, 1, 10, 1, -1, 0, sprite_alien1, 0x00FF00); /* green alien */
  init_object(3, 1, 4, 1, 18, 1, -1, 0, sprite_alien2, 0xFF0000); /* red alien */
  init_object(4, 1, 4, 1, 26, 1, -1, 0, sprite_alien3, 0xFF00FF); /* magenta alien */
  init_object(5, 1, 4, 1, 14, 3, -1, 0, sprite_alien4, 0xFFFF00); /* yellow alien */
  init_object(6, 1, 4, 1, 22, 3, -1, 0, sprite_alien5, 0x00FFFF);  /* cyan alien */

	/* initialization stage */
	push_state = 0;           /* no button pressed at beginning */
	alien_state = 0;          /* state of alien in a line */
	edge_reached = 0;         /* no edge reached at beginning */
	n_aliens = NOBJ - 2; /* number of displayed aliens */
	spaceship = object;   /* spaceship is the first declared object */
	laser = object + 1;       /* laser is the second declared object */



  clear_screen(0xcccccc);
  set_display_scale(8);
  set_bg_color(0xcccccc);
  set_fg_color(0x000000);
  int started = 0;
  while(!started){
    char msg[60];
    i = 0;
    msg[i] = 'P'; i = i + 1;
    msg[i] = 'o'; i = i + 1;
    msg[i] = 'u'; i = i + 1;
    msg[i] = 'r'; i = i + 1;
    msg[i] = ' '; i = i + 1;
    msg[i] = 'c'; i = i + 1;
    msg[i] = 'o'; i = i + 1;
    msg[i] = 'm'; i = i + 1;
    msg[i] = 'm'; i = i + 1;
    msg[i] = 'e'; i = i + 1;
    msg[i] = 'n'; i = i + 1;
    msg[i] = 'c'; i = i + 1;
    msg[i] = 'e'; i = i + 1;
    msg[i] = 'r'; i = i + 1;
    msg[i] = ','; i = i + 1;
    msg[i] = ' '; i = i + 1;
    msg[i] = 'a'; i = i + 1;
    msg[i] = 'p'; i = i + 1;
    msg[i] = 'p'; i = i + 1;
    msg[i] = 'u'; i = i + 1;
    msg[i] = 'y'; i = i + 1;
    msg[i] = 'e'; i = i + 1;
    msg[i] = 'z'; i = i + 1;
    msg[i] = ' '; i = i + 1;
    msg[i] = 's'; i = i + 1;
    msg[i] = 'u'; i = i + 1;
    msg[i] = 'r'; i = i + 1;
    msg[i] = ' '; i = i + 1;
    msg[i] = 'H'; i = i + 1;
    msg[i] = 'A'; i = i + 1;
    msg[i] = 'U'; i = i + 1;
    msg[i] = 'T'; i = i + 1;
    msg[i] = '\n'; i = i + 1;
    msg[i] = '\n'; i = i + 1;
    msg[i] = 'P'; i = i + 1;
    msg[i] = 'o'; i = i + 1;
    msg[i] = 'u'; i = i + 1;
    msg[i] = 'r'; i = i + 1;
    msg[i] = ' '; i = i + 1;
    msg[i] = 'q'; i = i + 1;
    msg[i] = 'u'; i = i + 1;
    msg[i] = 'i'; i = i + 1;
    msg[i] = 't'; i = i + 1;
    msg[i] = 't'; i = i + 1;
    msg[i] = 'e'; i = i + 1;
    msg[i] = 'r'; i = i + 1;
    msg[i] = ','; i = i + 1;
    msg[i] = ' '; i = i + 1;
    msg[i] = 'a'; i = i + 1;
    msg[i] = 'p'; i = i + 1;
    msg[i] = 'p'; i = i + 1;
    msg[i] = 'u'; i = i + 1;
    msg[i] = 'y'; i = i + 1;
    msg[i] = 'e'; i = i + 1;
    msg[i] = 'z'; i = i + 1;
    msg[i] = ' '; i = i + 1;
    msg[i] = 's'; i = i + 1;
    msg[i] = 'u'; i = i + 1;
    msg[i] = 'r'; i = i + 1;
    msg[i] = ' '; i = i + 1;
    msg[i] = 'B'; i = i + 1;
    msg[i] = 'A'; i = i + 1;
    msg[i] = 'S'; i = i + 1;
    msg[i] = 0; i = i + 1;


    set_display_cur_pos(10,10);
    display_string(msg);

    push_state = push_button_get();
    if (push_state & 0x4) {
      started = 1;
    }
    if (push_state & 0x8) {
      dirty_exit();
    }
  }

  clear_screen(0x333333);

  initialize();

	/* display stage */
	while(1==1) {
		edge_reached=0;

    /* decrease deadline of alive objects */
    i = 0;
		while (i < NOBJ) {
			if ((object[i]).alive == 1){
				(object[i]).deadline = (object[i]).deadline - 1;
      }
      i = i + 1;
		}

		/* display all alive objects */
    i = 0;
		while (i < NOBJ) {
			if ((object[i]).alive == 1){
				display_sprite(object + i);
      }
      i = i + 1;
		}

		/* determine new positions of all alive objects */
    i = 0;
		while(i < NOBJ) {
			/* update object state when deadline is reached */
			if ((object[i]).alive == 1 && (object[i]).deadline == 0) {
				/* reinitialize the object deadline to period */
				(object[i]).deadline = (object[i]).period;
				/* determine new position and manage screen edges */
				(object[i]).x = (object[i]).x + (object[i]).dx;
				if ((object[i]).x < 0){
					(object[i]).x = 0;
        }
				if ((object[i]).x > 59){
					(object[i]).x = 59;
        }
				(object[i]).y = (object[i]).y + (object[i]).dy;
				/* test if an edge of the screen was reached by an alien */
				if (i >= 2 && ((object[i]).x == 0 || (object[i]).x == 59)){
					edge_reached = 1;
        }
				if (i > 1 && (object[i]).y >= spaceship->y){
          // PERDU
					clear_screen(0x0000FF); /* blue screen */
          timer_set_and_wait(TIMER_FREQ, 1000);
          dirty_exit();
				}
			}
      i = i + 1;
		}
    /* test if alien is hit by an alive laser */
		if (laser->alive) {
      i = 2;
			while(i < NOBJ) {
				if ((object[i]).alive && !((laser->x > (object[i]).x + 1) || (laser->x + 1 < (object[i]).x)) &&
            (laser->y) == (object[i]).y) {
					n_aliens = n_aliens - 1;
					(object[i]).alive = 0;
					laser->alive = 0;
					if (n_aliens == 0) {
						/* no more aliens */
						spaceship->alive = 0;
						clear_screen(0xFF00); /* yellow screen */
            timer_set_and_wait(TIMER_FREQ, 1000);
            push_state = 0;           /* no button pressed at beginning */
            alien_state = 0;          /* state of alien in a line */
            edge_reached = 0;         /* no edge reached at beginning */
            n_aliens = NOBJ - 2; /* number of displayed aliens */
            spaceship = object;   /* spaceship is the first declared object */
            laser = object + 1;       /* laser is the second declared object */
            initialize();
					} else {
						display_sprite(object + i);
						display_sprite(laser);
					}
				}
        i = i + 1;
			}
		}
    /* when an alien reaches a screen edge, the group of aliens is moved */
		if (edge_reached) {
      i = 2;
			while (i < NOBJ) {
				(object[i]).dx = (etats[alien_state]).dx;
				(object[i]).dy = (etats[alien_state]).dy;
        i = i + 1;
			}
			alien_state = (etats[alien_state]).etat_suivant;
		}
    /* laser disappears when it reaches the screen top */
		if (laser->alive && laser->y == 0) {
			laser->alive = 0;
			display_sprite(laser);
		}
		/* manage push buttons */
    push_state = push_button_get();

		if (   (spaceship->deadline == 1)
           || (n_aliens == 0)) {
			spaceship->dx = 0;
			if (push_state & 0x1) { // right
        spaceship->dx = 1;
      }
			if (push_state & 0x2){ // left
				spaceship->dx = -1;
      }
			if (push_state & 0x4) { // fire
				if (!laser->alive) {
					laser->alive = 1;
					laser->dx = 0;
					laser->dy = -1;
					laser->x = spaceship->x;
					laser->y = spaceship->y - 1;
					laser->deadline = laser->period;
				}
			}
      if (push_state & 0x8) {
        dirty_exit();
			}
		}

    timer_set_and_wait(TIMER_FREQ, 4);
	}
}
