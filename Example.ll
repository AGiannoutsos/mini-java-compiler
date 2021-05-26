@.MulInherit_table = global [0 x i8*] []
@.A_table = global [3 x i8*] [i8* bitcast (i32 (i8* i32 i1 i8*)* @A.foo to i8*), i8* bitcast (i32 (i8*)* @A.bla to i8*), i8* bitcast (i32* (i8* i32*)* @A.foo2 to i8*)]
@.B_table = global [1 x i8*] [i8* bitcast (i32 (i8*)* @B.bla to i8*)]
@.C_table = global [0 x i8*] []


declare i8* @calloc(i32, i32)
declare i32 @printf(i8*, ...)
declare void @exit(i32)

@_cint = constant [4 x i8] c"%d\0a\00"
@_cOOB = constant [15 x i8] c"Out of bounds\0a\00"
define void @print_int(i32 %i) {
	%_str = bitcast [4 x i8]* @_cint to i8*
	call i32 (i8*, ...) @printf(i8* %_str, i32 %i)
	ret void
}

define void @throw_oob() {
	%_str = bitcast [15 x i8]* @_cOOB to i8*
	call i32 (i8*, ...) @printf(i8* %_str)
	call void @exit(i32 1)
	ret void
}

define i32 @main() {
	ret i32 0
}

define i32 @A.foo(i8* %this, i32 %.aa, i1 %.bb, i8* %.cc) {
	%aa = alloca i32
	store i32 %.aa, i32* %aa
	%bb = alloca i1
	store i1 %.bb, i1* %bb
	%cc = alloca i8*
	store i8* %.cc, i8** %cc
	ret i32 0
}

define i32 @A.bla(i8* %this) {
	%kk = alloca i32
	ret i32 0
}

define i32* @A.foo2(i8* %this, i32* %.arr) {
	%arr = alloca i32*
	store i32* %.arr, i32** %arr
	ret i32* 0
}

define i32 @B.bla(i8* %this) {
	%c = alloca i8*
	ret i32 0
}

