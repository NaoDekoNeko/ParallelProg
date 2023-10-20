# Primero, definimos la matriz
matriz = [1 2 3; 4 5 6; 7 8 9]

# Luego, abrimos el archivo en modo de escritura
open("matriz.txt", "w") do f
    # Iteramos sobre las filas de la matriz
    for fila in eachrow(matriz)
        # Escribimos cada fila en el archivo, separando los elementos con espacios
        println(f, join(fila, " "))
    end
end
