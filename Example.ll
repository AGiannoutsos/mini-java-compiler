@.Main_vtable = global [0 x i8*] []
@.D_vtable = global [4 x i8*] [i8* bitcast (i32 (i8*)* @D.a to i8*), i8* bitcast (i32 (i8*)* @B.b to i8*), i8* bitcast (i32 (i8*)* @C.c to i8*), i8* bitcast (i32 (i8*)* @D.d to i8*)]
@.C_vtable = global [3 x i8*] [i8* bitcast (i32 (i8*)* @B.a to i8*), i8* bitcast (i32 (i8*)* @B.b to i8*), i8* bitcast (i32 (i8*)* @C.c to i8*)]
@.B_vtable = global [2 x i8*] [i8* bitcast (i32 (i8*)* @B.a to i8*), i8* bitcast (i32 (i8*)* @B.b to i8*)]
@.A_vtable = global [1 x i8*] [i8* bitcast (i32 (i8*)* @A.a to i8*)]


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
	%a = alloca i8*
	store i8* null, i8** %a
	%_1 = call i8* @calloc(i32 1, i32 24)
	%_2 = bitcast i8* %_1 to i8*** 
	%_3 = getelementptr [4 x i8*], [4 x i8*]* @.D_vtable, i32 0, i32 0
	store i8** %_3, i8*** %_2
	store i8* %_1, i8** %a
	%_4 = load i8*, i8** %a
	; D.a : 0
	%_5 = bitcast i8* %_4 to i8*** 
	%_6 = load i8**, i8*** %_5
	%_7 = getelementptr i8*, i8** %_6, i32 0
	%_8 = load i8*, i8** %_7
	%_9 = bitcast i8* %_8 to i32 (i8*)* 
	%_10 = call i32 %_9(i8* %_4)
	call void (i32) @print_int(i32 %_10)
	ret i32 0
}

define i32 @A.a(i8* %this) {
	%_11 = getelementptr i8, i8* %this, i32 8
	%_12 = bitcast i8* %_11 to i32* 
	%_13 = load i32, i32* %_12
	ret i32 %_13
}

define i32 @B.b(i8* %this) {
	%_14 = getelementptr i8, i8* %this, i32 8
	%_15 = bitcast i8* %_14 to i32* 
	%_16 = load i32, i32* %_15
	ret i32 %_16
}

define i32 @B.a(i8* %this) {
	%_17 = getelementptr i8, i8* %this, i32 8
	%_18 = bitcast i8* %_17 to i32* 
	%_19 = load i32, i32* %_18
	ret i32 %_19
}

define i32 @C.c(i8* %this) {
	%_20 = getelementptr i8, i8* %this, i32 8
	%_21 = bitcast i8* %_20 to i32* 
	%_22 = load i32, i32* %_21
	ret i32 %_22
}

define i32 @D.a(i8* %this) {
	%_23 = getelementptr i8, i8* %this, i32 8
	%_24 = bitcast i8* %_23 to i32* 
	%_25 = load i32, i32* %_24
	ret i32 %_25
}

define i32 @D.d(i8* %this) {
	%_26 = getelementptr i8, i8* %this, i32 20
	%_27 = bitcast i8* %_26 to i32* 
	%_28 = load i32, i32* %_27
	ret i32 %_28
}

