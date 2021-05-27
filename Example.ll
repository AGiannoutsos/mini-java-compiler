@.C_vtable = global [0 x i8*] []
@.MulInherit_vtable = global [0 x i8*] []
@.B_vtable = global [2 x i8*] [i8* bitcast (i32 (i8*)* @B.bla to i8*), i8* bitcast (i32 (i8*)* @B.bla2 to i8*)]
@.A_vtable = global [5 x i8*] [i8* bitcast (i1 (i8*)* @A.bo1 to i8*), i8* bitcast (i1 (i8*)* @A.bo2 to i8*), i8* bitcast (i8* (i8*, i32, i8*, i8*)* @A.foo to i8*), i8* bitcast (i32 (i8*)* @A.bla to i8*), i8* bitcast (i32* (i8*, i32*)* @A.foo2 to i8*)]


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
	%a = alloca i8*
	%i = alloca i32
	%boo = alloca i1
	%_1 = call i8* @calloc(i32 1, i32 21)
	%_2 = bitcast i8* %_1 to i8*** 
	%_3 = getelementptr [5 x i8*], [5 x i8*]* @.A_vtable, i32 0, i32 0
	store i8** %_3, i8*** %_2
	store i8* %_1, i8** %a
	%_4 = load i8*, i8** %a
	; A.foo : 2
	%_5 = bitcast i8* %_4 to i8*** 
	%_6 = load i8**, i8*** %_5
	%_7 = getelementptr i8*, i8** %_6, i32 2
	%_8 = load i8*, i8** %_7
	%_9 = bitcast i8* %_8 to i8* (i8*, i32, i8*, i8*)* 
	%_10 = add i32 0, 74
	%_11 = call i8* @calloc(i32 1, i32 21)
	%_12 = bitcast i8* %_11 to i8*** 
	%_13 = getelementptr [5 x i8*], [5 x i8*]* @.A_vtable, i32 0, i32 0
	store i8** %_13, i8*** %_12
	; A.foo : 2
	%_14 = bitcast i8* %_11 to i8*** 
	%_15 = load i8**, i8*** %_14
	%_16 = getelementptr i8*, i8** %_15, i32 2
	%_17 = load i8*, i8** %_16
	%_18 = bitcast i8* %_17 to i8* (i8*, i32, i8*, i8*)* 
	%_19 = add i32 0, 1
	%_20 = call i8* @calloc(i32 1, i32 21)
	%_21 = bitcast i8* %_20 to i8*** 
	%_22 = getelementptr [5 x i8*], [5 x i8*]* @.A_vtable, i32 0, i32 0
	store i8** %_22, i8*** %_21
	%_23 = call i8* @calloc(i32 1, i32 8)
	%_24 = bitcast i8* %_23 to i8*** 
	%_25 = getelementptr [0 x i8*], [0 x i8*]* @.C_vtable, i32 0, i32 0
	store i8** %_25, i8*** %_24
	%_26 = call i8* %_18(i8* %_11, i32 %_19, i8* %_20, i8* %_23)
	%_27 = call i8* @calloc(i32 1, i32 8)
	%_28 = bitcast i8* %_27 to i8*** 
	%_29 = getelementptr [0 x i8*], [0 x i8*]* @.C_vtable, i32 0, i32 0
	store i8** %_29, i8*** %_28
	%_30 = call i8* %_9(i8* %_4, i32 %_10, i8* %_26, i8* %_27)
	; A.bla : 3
	%_31 = bitcast i8* %_30 to i8*** 
	%_32 = load i8**, i8*** %_31
	%_33 = getelementptr i8*, i8** %_32, i32 3
	%_34 = load i8*, i8** %_33
	%_35 = bitcast i8* %_34 to i32 (i8*)* 
	%_36 = call i32 %_35(i8* %_30)
	store i32 %_36, i32* %i
	%_37 = load i32, i32* %i
	call void (i32) @print_int(i32 %_37)
	%_38 = load i8*, i8** %a
	; A.bo1 : 0
	%_39 = bitcast i8* %_38 to i8*** 
	%_40 = load i8**, i8*** %_39
	%_41 = getelementptr i8*, i8** %_40, i32 0
	%_42 = load i8*, i8** %_41
	%_43 = bitcast i8* %_42 to i1 (i8*)* 
	%_44 = call i1 %_43(i8* %_38)
	br label %Andif1
Andif1:
	br i1 %_44, label %Andif2, label %Andif3
Andif2:
	%_45 = load i8*, i8** %a
	; A.bo2 : 1
	%_46 = bitcast i8* %_45 to i8*** 
	%_47 = load i8**, i8*** %_46
	%_48 = getelementptr i8*, i8** %_47, i32 1
	%_49 = load i8*, i8** %_48
	%_50 = bitcast i8* %_49 to i1 (i8*)* 
	%_51 = call i1 %_50(i8* %_45)
	br label %Andif3
Andif3:
	br label %Andif4
Andif4:
	%_52 = phi i1 [0, %Andif1], [%_51, %Andif3]
	store i1 %_52, i1* %boo
	%_53 = load i1, i1* %boo
	br i1 %_53, label %if5, label %if6
if5:
	%_54 = add i32 0, 3333
	call void (i32) @print_int(i32 %_54)
	br label %if7
if6:
	%_55 = add i32 0, 4444
	call void (i32) @print_int(i32 %_55)
	br label %if7
if7:
	ret i32 0
}

define i1 @A.bo1(i8* %this) {
	%_56 = add i32 0, 1111
	call void (i32) @print_int(i32 %_56)
	%_57 = add i1 0, 1
	ret i1 %_57
}

define i1 @A.bo2(i8* %this) {
	%_58 = add i32 0, 2222
	call void (i32) @print_int(i32 %_58)
	%_59 = add i1 0, 0
	ret i1 %_59
}

define i8* @A.foo(i8* %this, i32 %.aa, i8* %.bb, i8* %.cc) {
	%aa = alloca i32
	store i32 %.aa, i32* %aa
	%bb = alloca i8*
	store i8* %.bb, i8** %bb
	%cc = alloca i8*
	store i8* %.cc, i8** %cc
	%a = alloca i8*
	%_60 = getelementptr i8, i8* %this, i32 8
	%_61 = bitcast i8* %_60 to i32* 
	%_62 = load i32, i32* %aa
	store i32 %_62, i32* %_61
	%_63 = add i32 0, 777
	call void (i32) @print_int(i32 %_63)
	%_64 = getelementptr i8, i8* %this, i32 12
	%_65 = bitcast i8* %_64 to i32* 
	%_66 = load i32, i32* %_65
	call void (i32) @print_int(i32 %_66)
	%_67 = call i8* @calloc(i32 1, i32 21)
	%_68 = bitcast i8* %_67 to i8*** 
	%_69 = getelementptr [5 x i8*], [5 x i8*]* @.A_vtable, i32 0, i32 0
	store i8** %_69, i8*** %_68
	store i8* %_67, i8** %a
	%_70 = load i8*, i8** %a
	ret i8* %_70
}

define i32 @A.bla(i8* %this) {
	%kk = alloca i32
	%_71 = add i32 0, 44
	store i32 %_71, i32* %kk
	%_72 = add i32 0, 55
	ret i32 %_72
}

define i32* @A.foo2(i8* %this, i32* %.arr) {
	%arr = alloca i32*
	store i32* %.arr, i32** %arr
	%_73 = add i32 0, 10
	%_74 = icmp slt i32 %_73, 0
	br i1 %_74, label %Arif8, label %Arif9
Arif8:
	call void @throw_oob()
	br label %Arif9
Arif9:
	%_75 = add i32 %_73, 1
	%_76 = call i8* @calloc(i32 4, i32 %_75)
	%_77 = bitcast i8* %_76 to i32* 
	%_78 = getelementptr i32, i32* %_77, i32 0
	store i32 %_73, i32* %_78
	ret i32* %_77
}

define i32 @B.bla(i8* %this) {
	%c = alloca i8*
	%_79 = add i32 0, 66
	ret i32 %_79
}

define i32 @B.bla2(i8* %this) {
	%c = alloca i8*
	%_80 = add i32 0, 1
	ret i32 %_80
}

