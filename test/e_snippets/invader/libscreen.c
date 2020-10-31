#include "femto.h"
#include "libscreen.h"
#include "cep_platform.h"

typedef unsigned int uint;

volatile uint* IMG  = (volatile uint*) 0x80000000;
volatile uint *push = (volatile uint *)0x30000008;
volatile uint* led = (volatile uint *)REG_LEDS_ADDR;
volatile uint *timer     = (volatile uint *)CLINT_TIMER;
volatile uint *timer_hi =  (volatile uint *)CLINT_TIMER_HI;
volatile uint *timer_lo =  (volatile uint *)CLINT_TIMER_LOW;
volatile uint *timer_cmp = (volatile uint *)CLINT_TIMER_CMP;
volatile uint *timer_cmp_hi = (volatile uint *)CLINT_TIMER_CMP_HI;
volatile uint *timer_cmp_lo = (volatile uint *)CLINT_TIMER_CMP_LO;



/* function to get the state of push buttons */
uint push_button_get(void)
{
  uint v = (*push) >> 16;
  //printf("push_button_get: @ %p, v = %x, %x\n", push, *push, v);
  return v;
}


/* function to set the value displayed on leds */
void led_set(uint value)
{

	*led = value;
}

/* function to set the timer to be reached in period*time/100 in the future */
void timer_set(uint period, uint time)
{
  uint now = *timer;
  *timer_cmp = now + ((uint)period/RATIO * time);
}

/* function to wait for timer zero value */
void timer_wait(void)
{
	while(*timer <=  *timer_cmp);
}

void timer_set_and_wait(uint period, uint time)
{
  timer_set(period, time);
  timer_wait();
}

void draw(uint color, uint x, uint y){
  uint pos = (y * NBCOL + x);
  if (pos < 1920 * 1080){
    *(IMG + pos) = color;
  }
}

void clear_screen(uint color){
  for(uint j = 0; j < NBROW; j++){
    for(uint i = 0; i < NBCOL; i++){
      draw(color, i, j);
    }
  }
}

void draw_bitmap(char* bitmap){

  for(uint j = 0; j < 8; j++){
    for(uint i = 0; i < 8; i++){
      uint set = (bitmap[j] & (1 << i)) >> i;
      //draw pixel bitmap[j][i]
      for(uint jy = 0; jy < display_scale; jy++){
        for(uint ix = 0; ix < display_scale; ix++){
          uint realx = display_cur_x + i * display_scale + ix;
          uint realy = display_cur_y + j * display_scale + jy;
          /* pruintf("Writing at %d, %d, color = %x\n", realx, realy, color); */
          if(set){
            draw(fgcolor, realx, realy);
          }
          else{
            draw(bgcolor, realx, realy);
          }
        }
      }
    }
  }
}

extern char* itoa(uint, char*);

void newline(){
  display_cur_x = 0;
  display_cur_y += display_scale*10;
}
void tab(){
  display_cur_x += display_scale*8*4;
}

/* Counts the number of characters of current word. Will be used to break lines, if possible not in the middle of words. */
uint num_characters_until_white(char* str){
  uint i = 0;
  char c;
  while(c = *str++){
    if (c == ' ' || c == '\t' || c == '\n'){
      return i;
    }
    if (c == '.' || c == ','){
      return i + 1;
    }
    i++;
  }
  return i;
}



void display_string(char* str){
  while(*str){
    uint n = num_characters_until_white(str) + 1;
    // If there's not enough space on current line for whole current word, newline
    if (display_cur_x + (n-1) * display_scale * 8 > 1920){
      newline();
    }
    for(uint i = 0; i < n; i++){
      char c = str[i];
      if(c == '\n'){
        newline();
      } else if (c == '\t'){
        tab();
      } else {
        draw_bitmap(font8x8_basic[c]);
        display_cur_x+=display_scale*8;
      }
      // Still, if the next character wouldn't fit on the screen, break in the middle of the word.
      if (display_cur_x + display_scale * 8 > 1920){
        newline();
      }
    }
    str = str + n;
  }
}

void display_uint(uint i){
  char buf[10], *bu;
  bu = itoa(i, buf);
  display_string(bu);
}

void set_display_scale(int s){
  display_scale = s;
}
void set_display_cur_pos(int x, int y){
  display_cur_x = x;
  display_cur_y = y;
}


void set_fg_color(uint color){
  fgcolor = color;
}
void set_bg_color(uint color){
  bgcolor = color;
}



/* function to read a pixel from a (x,y) position of video framebuffer */
uint read_pixel(uint x, uint y, uint scale)
{
  // #SCALING
  //return IMG[y * DISPLAY_WIDTH + x];
	const uint pos = y * scale * NBCOL + x * scale;
  if (pos < 1920*1080)
    return IMG[pos];
  return -1;
}

/* function to write a pixel in a (x,y) position of video framebuffer */
void write_pixel(uint pixel, uint x, uint y)
{
  const uint pos = y * NBCOL + x;
  if (pos < 1920*1080)
  IMG[pos] = pixel;
}

void write_pixel_scaling(uint pixel, uint x, uint y, uint scale)
{
  uint i, j;
	for (i = 0; i < scale; ++i) {
		for (j = 0; j < scale; ++j) {
			const uint real_y = (y * scale + i);
			const uint real_x = x * scale + j;

      write_pixel(pixel, real_x, real_y);
		}
	}

}

void show_pos ( int i, int x, int y){
  /* printf("show_pos %d: @ %p, v = %x\n",x,  ptr, *(int*)(ptr+20)); */
  /* printf("alive = %x\n", *(int*)(ptr+ 0 )); */
  /* printf("period = %x\n", *(int*)(ptr+ 4 )); */
  /* printf("deadline = %x\n", *(int*)(ptr+ 8 )); */
  /* printf("x = %x\n", *(int*)(ptr+ 12 )); */
  /* printf("y = %x\n", *(int*)(ptr+ 16 )); */
  /* printf("dx = %x\n", *(int*)(ptr+ 20 )); */
  /* printf("dy = %x\n", *(int*)(ptr+ 24 )); */
  /* int i; */
  /* asm("\t mv %0, sp" : "=r"(i)); */
  /* printf("sp = %x\n", i); */
  /* asm("\t mv %0, s0" : "=r"(i)); */
  /* printf("s0 = %x\n", i); */
  printf("i = %d, x = %d; y = %d\n",i, x,y);
}
