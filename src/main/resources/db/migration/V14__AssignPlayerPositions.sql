-- V14: Asignar posiciones a los jugadores de prueba del Manchester City

-- Portero
UPDATE users SET position = 'PORTERO'  WHERE name = 'Ederson';

-- Defensas
UPDATE users SET position = 'DEFENSA'  WHERE name = 'Kyle Walker';
UPDATE users SET position = 'DEFENSA'  WHERE name = 'Ruben Dias';
UPDATE users SET position = 'DEFENSA',  secondary_position = 'MEDIO'    WHERE name = 'John Stones';

-- Medios
UPDATE users SET position = 'MEDIO',   secondary_position = 'DEFENSA'   WHERE name = 'Rodri';
UPDATE users SET position = 'MEDIO',   secondary_position = 'ATACANTE'  WHERE name = 'Kevin De Bruyne';
UPDATE users SET position = 'MEDIO',   secondary_position = 'ATACANTE'  WHERE name = 'Bernardo Silva';
UPDATE users SET position = 'MEDIO',   secondary_position = 'ATACANTE'  WHERE name = 'Phil Foden';

-- Atacantes
UPDATE users SET position = 'ATACANTE', secondary_position = 'MEDIO'    WHERE name = 'Jack Grealish';
UPDATE users SET position = 'ATACANTE', secondary_position = 'MEDIO'    WHERE name = 'Julián Álvarez';
UPDATE users SET position = 'ATACANTE'  WHERE name = 'Jeremy Doku';
UPDATE users SET position = 'ATACANTE'  WHERE name = 'Oscar Bobb';
UPDATE users SET position = 'ATACANTE'  WHERE name = 'Erling Haaland';
