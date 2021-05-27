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
	%arr = alloca i32*
	%_1 = add i32 0, 2
	%_2 = icmp slt i32 %_1, 0
	br i1 %_2, label %Arif1, label %Arif2
Arif1:
	call void @throw_oob()
	br label %Arif2
Arif2:
	%_3 = add i32 %_1, 1
	%_4 = call i8* @calloc(i32 4, i32 %_3)
	%_5 = bitcast i8* %_4 to i32* 
	%_6 = getelementptr i32, i32* %_5, i32 0
	store i32 %_1, i32* %_6
	store i32* %_5, i32** %arr
	%_7 = load i32*, i32** %arr
	%_8 = add i32 0, 1
	%_9 = add i32 0, 100
	%_10 = getelementptr i32, i32* %_7, i32 0
	%_11 = load i32, i32* %_10
	%_12 = icmp slt i32 %_8, %_11
	%_13 = icmp slt i32 -1, %_8
	%_14 = and i1 %_13, %_12
	br i1 %_14, label %Arif4, label %Arif3
Arif3:
	call void @throw_oob()
	br label %Arif4
Arif4:
	%_15 = add i32 %_8, 1
	%_16 = getelementptr i32, i32* %_7, i32 %_15
	store i32 %_9, i32* %_16
	%_17 = load i32*, i32** %arr
	%_18 = add i32 0, 0
	%_19 = getelementptr i32, i32* %_17, i32 0
	%_20 = load i32, i32* %_19
	%_21 = icmp slt i32 %_18, %_20
	%_22 = icmp slt i32 -1, %_18
	%_23 = and i1 %_22, %_21
	br i1 %_23, label %Arif6, label %Arif5
Arif5:
	call void @throw_oob()
	br label %Arif6
Arif6:
	%_24 = add i32 %_18, 1
	%_25 = getelementptr i32, i32* %_17, i32 %_24
	%_26 = load i32, i32* %_25
	call void (i32) @print_int(i32 %_26)
	%_27 = load i32*, i32** %arr
	%_28 = add i32 0, 1
	%_29 = getelementptr i32, i32* %_27, i32 0
	%_30 = load i32, i32* %_29
	%_31 = icmp slt i32 %_28, %_30
	%_32 = icmp slt i32 -1, %_28
	%_33 = and i1 %_32, %_31
	br i1 %_33, label %Arif8, label %Arif7
Arif7:
	call void @throw_oob()
	br label %Arif8
Arif8:
	%_34 = add i32 %_28, 1
	%_35 = getelementptr i32, i32* %_27, i32 %_34
	%_36 = load i32, i32* %_35
	call void (i32) @print_int(i32 %_36)
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
	%_37 = getelementptr i8, i8* %this, i32 4
	%_38 = bitcast i8* %_37 to i32* 
	%_39 = add i32 0, 666
	store i32 %_39, i32* %_38
	%_40 = add i32 0, 777
	call void (i32) @print_int(i32 %_40)
	%_41 = getelementptr i8, i8* %this, i32 4
	%_42 = bitcast i8* %_41 to i32* 
	%_43 = load i32, i32* %_42
	call void (i32) @print_int(i32 %_43)
	%_44 = add i32 0, 1
	ret i32 %_44
}

define i32 @A.bla(i8* %this) {
	%kk = alloca i32
	%_45 = add i32 0, 1
	store i32 %_45, i32* %kk
	%_46 = add i32 0, 1
	ret i32 %_46
}

define i32* @A.foo2(i8* %this, i32* %.arr) {
	%arr = alloca i32*
	store i32* %.arr, i32** %arr
	%_47 = add i32 0, 10
	%_48 = icmp slt i32 %_47, 0
	br i1 %_48, label %Arif9, label %Arif10
Arif9:
	call void @throw_oob()
	br label %Arif10
Arif10:
	%_49 = add i32 %_47, 1
	%_50 = call i8* @calloc(i32 4, i32 %_49)
	%_51 = bitcast i8* %_50 to i32* 
	%_52 = getelementptr i32, i32* %_51, i32 0
	store i32 %_47, i32* %_52
	ret i32* %_51
}

define i32 @B.bla(i8* %this) {
	%c = alloca i8*
	%_53 = add i32 0, 1
	ret i32 %_53
}

define i32 @B.bla2(i8* %this) {
	%c = alloca i8*
	%_54 = add i32 0, 1
	ret i32 %_54
}

