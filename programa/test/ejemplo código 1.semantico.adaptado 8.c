
    float~func1 <| char~x22 |>  |: ¡¡semantico borrar parametro
	string~sss<-"Hola"!
	float~ff <--0.01! ¡¡semántico, aplica global
	int~numero! int~g! char~letra! float~f! ¡¡ semantico, verificar que hayan sido asignados 
	numero <- 10! 
	g <- numero + 10! ¡¡ semantico inicializacion
	numero <- 10 * numero / numero! ¡¡ semantico
	numero <- - numero! ¡¡ confirmacion de error negativo
	letra <- 'w'! ¡¡ semantico asignacion tipo
	char~letra2 <- 'b'! ¡¡ semantico no existente
	cout <| numero |> !
	cout <| letra |> ! ¡¡ semantico tipo imprime
	cin <| f |> ! ¡¡ semantico tipo lee, cambiar por f y validar inicializacion adelante
	int~numS <- numero - 1 * numero + numero !
	cout <| numS |> !

	char~x20<-'a'! ¡¡semantico x22 duplicado
	char~_miChar_<-'!' !
	char~_miChar2_<-'!'! 
	int~_x30_<--1!
	bool~_x40_<-false!
	float~_x50_<<1>><<2>> <- |:|:4.0,5.0:|:|! ¡¡semantico tipo
	float~_x502_<<1>><<2>> <- |:|:5.5,6.4:|:|! ¡¡semantico, tamaño
	string~_x500_<-"Hola a todos los que est<<a>> haciendo un compilador nuevo|:n"!
	_x30_ <- ++numero!¡¡semantico, probar con arreglo
	int ~var <- 6!
	if <| less_te<|23,45|> @ greather_t<|var,5|> |>  |:  ¡¡semantico x22, var __relacional__ x2
		int~y!
		x22<-'q'!¡¡semantico
		char~ch33<-'a'!
	 :|
	else |: 
		int~y! ¡¡ semantico no error duplicado en if-else
		string~str2<-"sdff"!
	 :|
	if <| n_equal<|false,true|> # greather_t<|_x50_<<0>><<1>>,5.6+1|> |>  |:  ¡¡semantico diferente logico __relacional__ x2
		int~y!
		x22<-'d'!¡¡semantico
		char~ch33<-'a'!
	 :|
	bool~_i_<- false!
	do  |: cout <| _i_ |> ! numero <- 33!  ff <--0.02 * -3.7!  :| while <|true|>!  ¡¡semantico condicion
	cout <| "Hola mundo" |> !

	switch <|1|> |: ¡¡semantico
		case~ numero: ¡¡semantico/sintactico
			cout<|g|>! g <- 20! cout<|ff|>!
			break!
		case~ 67: ¡¡semantico
			cout<|ff|>! g <- 21! cout<|ff|>!
		default:
			cout<|g|>! g <- 22! cout<|g|>!
	:|
	int ~x27!
	float ~x28!
	cin <| x27 |> ! ¡¡semantico tipo
	return~-5.6!¡¡cambio en retorno genera semantico
	 cin <| x28 |> ! ¡¡semantico ?
 :|  

 bool~_func2_  <| bool~_b1_, int~_i1_ |>   |: 

	return~ _b1_! ¡¡semantico generar error por ausencia, con -5.6 y con i1
  :|  

string~_func3_  <|  |>   |:  ¡¡semantico string~
	string~_b1_<-"Hola"!
	return~ _b1_! 
  :|

bool~_func5_  <| bool~b1|>   |: 
	int~j !
	return~ true! 
  :| 

  bool~_func6_  <| bool~b1|>   |:  ¡¡semantico funcion repetida
	int~j ! 
	if <| true |>  |:
		int~y!
		char~ch33<-'a'!
	 :|
	return~ true!
  :| 

empty~ __main__ <| |>  |: 
	char~miChar<-'!'!
	char~miChar2<-'!'! 
	string~str1<-"Mi string~1"!
	float~fl1!
	fl1<-56.6! ¡¡semantico fl1
	int~in1<-10! 
	int~in12<---in1- -14%++in1 + ++in1 / 15 * -30e50 / -10//3! ¡¡semantico fl1, in1, semantico division
	float~fl3<-3.7 ^ 2 + 10 + <| 45 % 76 |> ! ¡¡semantico literal 76, potencia
	
¡¡comentario 2
	float~arr<<1>><<2>> <- |:|:4.4,5.5:|:|!
	fl1 <- 4.5%2.0^0! ¡¡semantico miChar
	miFunc <| miFunc <|  |> ,'a' |> ! ¡¡semantico miFunc, hola
	bool~bl0 <- n_equal<|6.7*1.1,8.9|>! ¡¡ok __relacional__
	bl0 <- n_equal<| true, greather_t<|5,4|> |>! ¡¡ok __relacional__ x2
	bool~bl1 <- greather_te<|in1,fl1|> # false @ $ <| greather_t<|_func2_ <| true,1+1 |> , 56|> |> ! ¡¡semantico in1 >= fl1, func2 __relacional__ x2

 	do |:
        int~_i_!
	:| while <| greather_t<|++arr,12.2|> @ $ <| greather_t<|12, <|34+35|> |> |>  |> ! ¡¡semantico __relacional__ x2
 
	return~   bl1! ¡¡semantico
 :| 