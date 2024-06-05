SELECT 
    name, 
    artists, 
    release_date, 
    energy, 
    danceability
FROM 
    `formatted_spotify_dataset.spotify`
WHERE 
    year > 2010 
    AND energy > 0.8 
    AND danceability > 0.8
ORDER BY 
    release_date;
