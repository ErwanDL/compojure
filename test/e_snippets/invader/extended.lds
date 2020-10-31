OUTPUT_ARCH( "riscv" )

GROUP(-lgcc)

ENTRY( _start )

MEMORY
{
  ram   (wxa!ri) : ORIGIN = 0x1000, LENGTH = 32M
}

PHDRS
{
  text PT_LOAD;
  data PT_LOAD;
  bss PT_LOAD;
}

SECTIONS
{
  PROVIDE(_start = 0x1000);
  .text : {
    PROVIDE(_text_start = .);
    *(.text.init) *(.text .text.*)
    PROVIDE(_text_end = .);
  } >ram AT>ram :text

  .rodata : {
    PROVIDE(_rodata_start = .);
    *(.rodata .rodata.*)
    PROVIDE(_rodata_end = .);
  } >ram AT>ram :text

  .data : {
    . = ALIGN(4096);
    PROVIDE(_data_start = .);
    *(.sdata .sdata.*) *(.data .data.*)
    PROVIDE(_data_end = .);
  } >ram AT>ram :data

  .bss 0xc00000:{
    PROVIDE(stacks = .);
    PROVIDE(_bss_start = .);
    .  = . + 0x400;
    . = . + 0x100;
    *(.sbss .sbss.*) *(.bss .bss.*)
    *(COMMON)
    . = . + 0x100;
    PROVIDE(_bss_end = .);
  } >ram AT>ram :bss

  PROVIDE(_memory_start = ORIGIN(ram));
  PROVIDE(_memory_end = ORIGIN(ram) + LENGTH(ram));
}
