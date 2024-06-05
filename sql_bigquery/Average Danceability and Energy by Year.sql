SELECT 
    year, 
    AVG(danceability) AS avg_danceability, 
    AVG(energy) AS avg_energy
FROM 
    `formatted_spotify_dataset.spotify`
GROUP BY 
    year
ORDER BY 
    year;
