@.Main_vtable = global [0 x i8*] []
@.C_vtable = global [3 x i8*] [i8* bitcast (i32 (i8*)* @C.get_class_x to i8*), i8* bitcast (i32 (i8*)* @C.get_method_x to i8*), i8* bitcast (i1 (i8*)* @C.set_int_x to i8*)]
@.D_vtable = global [4 x i8*] [i8* bitcast (i32 (i8*)* @C.get_class_x to i8*), i8* bitcast (i32 (i8*)* @C.get_method_x to i8*), i8* bitcast (i1 (i8*)* @C.set_int_x to i8*), i8* bitcast (i1 (i8*)* @D.get_class_x2 to i8*)]
@.E_vtable = global [6 x i8*] [i8* bitcast (i32 (i8*)* @C.get_class_x to i8*), i8* bitcast (i32 (i8*)* @C.get_method_x to i8*), i8* bitcast (i1 (i8*)* @C.set_int_x to i8*), i8* bitcast (i1 (i8*)* @D.get_class_x2 to i8*), i8* bitcast (i1 (i8*)* @E.set_bool_x to i8*), i8* bitcast (i1 (i8*)* @E.get_bool_x to i8*)]
@.B_vtable = global [3 x i8*] [i8* bitcast (i1 (i8*)* @B.set_x to i8*), i8* bitcast (i32 (i8*)* @B.x to i8*), i8* bitcast (i32 (i8*)* @A.y to i8*)]
@.A_vtable = global [3 x i8*] [i8* bitcast (i1 (i8*)* @A.set_x to i8*), i8* bitcast (i32 (i8*)* @A.x to i8*), i8* bitcast (i32 (i8*)* @A.y to i8*)]


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
	%c = alloca i8*
	store i8* null, i8** %c
	%d = alloca i8*
	store i8* null, i8** %d
	%e = alloca i8*
	store i8* null, i8** %e
	%dummy = alloca i1
	store i1 false, i1* %dummy
	%_1 = call i8* @calloc(i32 1, i32 16)
	%_2 = bitcast i8* %_1 to i8*** 
	%_3 = getelementptr [3 x i8*], [3 x i8*]* @.A_vtable, i32 0, i32 0
	store i8** %_3, i8*** %_2
	store i8* %_1, i8** %a
	%_4 = load i8*, i8** %a
	; A.set_x : 0
	%_5 = bitcast i8* %_4 to i8*** 
	%_6 = load i8**, i8*** %_5
	%_7 = getelementptr i8*, i8** %_6, i32 0
	%_8 = load i8*, i8** %_7
	%_9 = bitcast i8* %_8 to i1 (i8*)* 
	%_10 = call i1 %_9(i8* %_4)
	store i1 %_10, i1* %dummy
	%_11 = load i8*, i8** %a
	; A.x : 1
	%_12 = bitcast i8* %_11 to i8*** 
	%_13 = load i8**, i8*** %_12
	%_14 = getelementptr i8*, i8** %_13, i32 1
	%_15 = load i8*, i8** %_14
	%_16 = bitcast i8* %_15 to i32 (i8*)* 
	%_17 = call i32 %_16(i8* %_11)
	call void (i32) @print_int(i32 %_17)
	%_18 = load i8*, i8** %a
	; A.y : 2
	%_19 = bitcast i8* %_18 to i8*** 
	%_20 = load i8**, i8*** %_19
	%_21 = getelementptr i8*, i8** %_20, i32 2
	%_22 = load i8*, i8** %_21
	%_23 = bitcast i8* %_22 to i32 (i8*)* 
	%_24 = call i32 %_23(i8* %_18)
	call void (i32) @print_int(i32 %_24)
	%_25 = call i8* @calloc(i32 1, i32 20)
	%_26 = bitcast i8* %_25 to i8*** 
	%_27 = getelementptr [3 x i8*], [3 x i8*]* @.B_vtable, i32 0, i32 0
	store i8** %_27, i8*** %_26
	store i8* %_25, i8** %a
	%_28 = load i8*, i8** %a
	; A.set_x : 0
	%_29 = bitcast i8* %_28 to i8*** 
	%_30 = load i8**, i8*** %_29
	%_31 = getelementptr i8*, i8** %_30, i32 0
	%_32 = load i8*, i8** %_31
	%_33 = bitcast i8* %_32 to i1 (i8*)* 
	%_34 = call i1 %_33(i8* %_28)
	store i1 %_34, i1* %dummy
	%_35 = load i8*, i8** %a
	; A.x : 1
	%_36 = bitcast i8* %_35 to i8*** 
	%_37 = load i8**, i8*** %_36
	%_38 = getelementptr i8*, i8** %_37, i32 1
	%_39 = load i8*, i8** %_38
	%_40 = bitcast i8* %_39 to i32 (i8*)* 
	%_41 = call i32 %_40(i8* %_35)
	call void (i32) @print_int(i32 %_41)
	%_42 = load i8*, i8** %a
	; A.y : 2
	%_43 = bitcast i8* %_42 to i8*** 
	%_44 = load i8**, i8*** %_43
	%_45 = getelementptr i8*, i8** %_44, i32 2
	%_46 = load i8*, i8** %_45
	%_47 = bitcast i8* %_46 to i32 (i8*)* 
	%_48 = call i32 %_47(i8* %_42)
	call void (i32) @print_int(i32 %_48)
	%_49 = call i8* @calloc(i32 1, i32 12)
	%_50 = bitcast i8* %_49 to i8*** 
	%_51 = getelementptr [3 x i8*], [3 x i8*]* @.C_vtable, i32 0, i32 0
	store i8** %_51, i8*** %_50
	store i8* %_49, i8** %c
	%_52 = load i8*, i8** %c
	; C.get_method_x : 1
	%_53 = bitcast i8* %_52 to i8*** 
	%_54 = load i8**, i8*** %_53
	%_55 = getelementptr i8*, i8** %_54, i32 1
	%_56 = load i8*, i8** %_55
	%_57 = bitcast i8* %_56 to i32 (i8*)* 
	%_58 = call i32 %_57(i8* %_52)
	call void (i32) @print_int(i32 %_58)
	%_59 = load i8*, i8** %c
	; C.get_class_x : 0
	%_60 = bitcast i8* %_59 to i8*** 
	%_61 = load i8**, i8*** %_60
	%_62 = getelementptr i8*, i8** %_61, i32 0
	%_63 = load i8*, i8** %_62
	%_64 = bitcast i8* %_63 to i32 (i8*)* 
	%_65 = call i32 %_64(i8* %_59)
	call void (i32) @print_int(i32 %_65)
	%_66 = call i8* @calloc(i32 1, i32 13)
	%_67 = bitcast i8* %_66 to i8*** 
	%_68 = getelementptr [4 x i8*], [4 x i8*]* @.D_vtable, i32 0, i32 0
	store i8** %_68, i8*** %_67
	store i8* %_66, i8** %d
	%_69 = load i8*, i8** %d
	; D.set_int_x : 2
	%_70 = bitcast i8* %_69 to i8*** 
	%_71 = load i8**, i8*** %_70
	%_72 = getelementptr i8*, i8** %_71, i32 2
	%_73 = load i8*, i8** %_72
	%_74 = bitcast i8* %_73 to i1 (i8*)* 
	%_75 = call i1 %_74(i8* %_69)
	store i1 %_75, i1* %dummy
	%_76 = load i8*, i8** %d
	; D.get_class_x2 : 3
	%_77 = bitcast i8* %_76 to i8*** 
	%_78 = load i8**, i8*** %_77
	%_79 = getelementptr i8*, i8** %_78, i32 3
	%_80 = load i8*, i8** %_79
	%_81 = bitcast i8* %_80 to i1 (i8*)* 
	%_82 = call i1 %_81(i8* %_76)
	br i1 %_82, label %if1, label %if2
if1:
	%_83 = add i32 0, 1
	call void (i32) @print_int(i32 %_83)
	br label %if3
if2:
	%_84 = add i32 0, 0
	call void (i32) @print_int(i32 %_84)
	br label %if3
if3:
	%_85 = call i8* @calloc(i32 1, i32 14)
	%_86 = bitcast i8* %_85 to i8*** 
	%_87 = getelementptr [6 x i8*], [6 x i8*]* @.E_vtable, i32 0, i32 0
	store i8** %_87, i8*** %_86
	store i8* %_85, i8** %e
	%_88 = load i8*, i8** %e
	; E.set_int_x : 2
	%_89 = bitcast i8* %_88 to i8*** 
	%_90 = load i8**, i8*** %_89
	%_91 = getelementptr i8*, i8** %_90, i32 2
	%_92 = load i8*, i8** %_91
	%_93 = bitcast i8* %_92 to i1 (i8*)* 
	%_94 = call i1 %_93(i8* %_88)
	store i1 %_94, i1* %dummy
	%_95 = load i8*, i8** %e
	; E.get_class_x2 : 3
	%_96 = bitcast i8* %_95 to i8*** 
	%_97 = load i8**, i8*** %_96
	%_98 = getelementptr i8*, i8** %_97, i32 3
	%_99 = load i8*, i8** %_98
	%_100 = bitcast i8* %_99 to i1 (i8*)* 
	%_101 = call i1 %_100(i8* %_95)
	br i1 %_101, label %if4, label %if5
if4:
	%_102 = add i32 0, 1
	call void (i32) @print_int(i32 %_102)
	br label %if6
if5:
	%_103 = add i32 0, 0
	call void (i32) @print_int(i32 %_103)
	br label %if6
if6:
	%_104 = load i8*, i8** %e
	; E.set_bool_x : 4
	%_105 = bitcast i8* %_104 to i8*** 
	%_106 = load i8**, i8*** %_105
	%_107 = getelementptr i8*, i8** %_106, i32 4
	%_108 = load i8*, i8** %_107
	%_109 = bitcast i8* %_108 to i1 (i8*)* 
	%_110 = call i1 %_109(i8* %_104)
	store i1 %_110, i1* %dummy
	%_111 = load i8*, i8** %e
	; E.get_bool_x : 5
	%_112 = bitcast i8* %_111 to i8*** 
	%_113 = load i8**, i8*** %_112
	%_114 = getelementptr i8*, i8** %_113, i32 5
	%_115 = load i8*, i8** %_114
	%_116 = bitcast i8* %_115 to i1 (i8*)* 
	%_117 = call i1 %_116(i8* %_111)
	br i1 %_117, label %if7, label %if8
if7:
	%_118 = add i32 0, 1
	call void (i32) @print_int(i32 %_118)
	br label %if9
if8:
	%_119 = add i32 0, 0
	call void (i32) @print_int(i32 %_119)
	br label %if9
if9:
	ret i32 0
}

define i1 @A.set_x(i8* %this) {
	%_120 = getelementptr i8, i8* %this, i32 8
	%_121 = bitcast i8* %_120 to i32* 
	%_122 = add i32 0, 1
	store i32 %_122, i32* %_121
	%_123 = add i1 0, 1
	ret i1 %_123
}

define i32 @A.x(i8* %this) {
	%_124 = getelementptr i8, i8* %this, i32 8
	%_125 = bitcast i8* %_124 to i32* 
	%_126 = load i32, i32* %_125
	ret i32 %_126
}

define i32 @A.y(i8* %this) {
	%_127 = getelementptr i8, i8* %this, i32 12
	%_128 = bitcast i8* %_127 to i32* 
	%_129 = load i32, i32* %_128
	ret i32 %_129
}

define i1 @B.set_x(i8* %this) {
	%_130 = getelementptr i8, i8* %this, i32 16
	%_131 = bitcast i8* %_130 to i32* 
	%_132 = add i32 0, 2
	store i32 %_132, i32* %_131
	%_133 = add i1 0, 1
	ret i1 %_133
}

define i32 @B.x(i8* %this) {
	%_134 = getelementptr i8, i8* %this, i32 16
	%_135 = bitcast i8* %_134 to i32* 
	%_136 = load i32, i32* %_135
	ret i32 %_136
}

define i32 @C.get_class_x(i8* %this) {
	%_137 = getelementptr i8, i8* %this, i32 8
	%_138 = bitcast i8* %_137 to i32* 
	%_139 = load i32, i32* %_138
	ret i32 %_139
}

define i32 @C.get_method_x(i8* %this) {
	%x = alloca i32
	store i32 0, i32* %x
	%_140 = add i32 0, 3
	store i32 %_140, i32* %x
	%_141 = load i32, i32* %x
	ret i32 %_141
}

define i1 @C.set_int_x(i8* %this) {
	%_142 = getelementptr i8, i8* %this, i32 8
	%_143 = bitcast i8* %_142 to i32* 
	%_144 = add i32 0, 20
	store i32 %_144, i32* %_143
	%_145 = add i1 0, 1
	ret i1 %_145
}

define i1 @D.get_class_x2(i8* %this) {
	%_146 = getelementptr i8, i8* %this, i32 12
	%_147 = bitcast i8* %_146 to i1* 
	%_148 = load i1, i1* %_147
	ret i1 %_148
}

define i1 @E.set_bool_x(i8* %this) {
	%_149 = getelementptr i8, i8* %this, i32 13
	%_150 = bitcast i8* %_149 to i1* 
	%_151 = add i1 0, 1
	store i1 %_151, i1* %_150
	%_152 = add i1 0, 1
	ret i1 %_152
}

define i1 @E.get_bool_x(i8* %this) {
	%_153 = getelementptr i8, i8* %this, i32 13
	%_154 = bitcast i8* %_153 to i1* 
	%_155 = load i1, i1* %_154
	ret i1 %_155
}

