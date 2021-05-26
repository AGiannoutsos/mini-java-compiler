@.MulInherit_table = global [0 x i8*] []
@.A_table = global [3 x i8*] [i8* bitcast (i32 (i8*, i32, i1, i8*)* @A.foo to i8*), i8* bitcast (i32 (i8*)* @A.bla to i8*), i8* bitcast (i32* (i8*, i32*)* @A.foo2 to i8*)]
@.B_table = global [2 x i8*] [i8* bitcast (i32 (i8*)* @B.bla to i8*), i8* bitcast (i32 (i8*)* @B.bla2 to i8*)]
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
	%test = alloca i32
	%best = alloca i32
	%a = alloca i1
	%_1 = add i1 0, 1
	store i1 %_1, i1* %a
	%_2 = load i1, i1* %a
	%_3 = load i1, i1* %a
	%_4 = and i1 %_2, %_3
	store i1 %_4, i1* %a
	%_5 = add i32 0, 10
	store i32 %_5, i32* %test
	%_6 = load i32, i32* %test
	%_7 = add i32 0, 20
	%_8 = icmp slt i32 %_6, %_7
	br i1 %_8, label %if1, label %if2
if1:
	%_9 = add i32 0, 1000
	call void (i32) @print_int(i32 %_9)
	br label %if3
if2:
	%_10 = add i32 0, 10
	call void (i32) @print_int(i32 %_10)
	br label %if3
if3:
	%_11 = add i32 0, 666
	%_12 = load i32, i32* %test
	%_13 = add i32 %_11, %_12
	%_14 = add i32 0, 1000
	%_15 = add i32 %_13, %_14
	call void (i32) @print_int(i32 %_15)
	%_16 = add i32 0, 777
	%_17 = add i32 0, 10
	%_18 = sub i32 %_16, %_17
	call void (i32) @print_int(i32 %_18)
	%_19 = load i32, i32* %test
	%_20 = add i32 0, 10
	%_21 = mul i32 %_19, %_20
	call void (i32) @print_int(i32 %_21)
	%_22 = load i32, i32* %best
	call void (i32) @print_int(i32 %_22)
	br label %Lif4
Lif4:
	%_23 = load i32, i32* %test
	%_24 = add i32 0, 20
	%_25 = icmp slt i32 %_23, %_24
	br i1 %_25, label %Lif5, label %Lif6
Lif5:
	%_26 = load i32, i32* %test
	%_27 = add i32 0, 1
	%_28 = add i32 %_26, %_27
	store i32 %_28, i32* %test
	%_29 = load i32, i32* %test
	call void (i32) @print_int(i32 %_29)
	br label %Lif4
Lif6:
	ret i32 0
}

define i32 @A.foo(i8* %this, i32 %.aa, i1 %.bb, i8* %.cc) {
	%aa = alloca i32
	store i32 %.aa, i32* %aa
	%bb = alloca i1
	store i1 %.bb, i1* %bb
	%cc = alloca i8*
	store i8* %.cc, i8** %cc
	%a1 = alloca i32
	%_30 = getelementptr i8, i8* %this, i32 4
	%_31 = bitcast i8* %_30 to i32* 
	%_32 = add i32 0, 666
	store i32 %_32, i32* %_31
	%_33 = add i32 0, 777
	call void (i32) @print_int(i32 %_33)
	%_34 = getelementptr i8, i8* %this, i32 4
	%_35 = bitcast i8* %_34 to i32* 
	%_36 = load i32, i32* %_35
	call void (i32) @print_int(i32 %_36)
	%_37 = add i32 0, 1
	ret i32 %_37
}

define i32 @A.bla(i8* %this) {
	%kk = alloca i32
	%_38 = add i32 0, 1
	store i32 %_38, i32* %kk
	%_39 = add i32 0, 1
	ret i32 %_39
}

define i32* @A.foo2(i8* %this, i32* %.arr) {
	%arr = alloca i32*
	store i32* %.arr, i32** %arr
	%_40 = add i32 0, 10
	ret i32* null
}

define i32 @B.bla(i8* %this) {
	%c = alloca i8*
	%_41 = add i32 0, 1
	ret i32 %_41
}

define i32 @B.bla2(i8* %this) {
	%c = alloca i8*
	%_42 = add i32 0, 1
	ret i32 %_42
}

