Parse.Cloud.define("update_spot", function(request, response) {
	var parkingId = request.params.parkingId;
	var spotId = request.params.spotId;
	var isFree = request.params.isFree;

  	var Spot = Parse.Object.extend("Spot");
  	var query = new Parse.Query(Spot);
  	query.equalTo("parkingId", parkingId);
  	query.equalTo("spotId", spotId);
  	query.first().then(function(result) {
  		result.set("isFree", isFree);
  		result.save();

  		Parse.Push.send({
			channels: [parkingId],
  			data: {
  					alert: spotId + "-" + isFree
  				}
  			}, null);
  	}).then(function() {
  		response.success("Successfuly updated the spot.");
  	}, function(error) {
  		response.error("Failed to update the spot.");
  	});
});

Parse.Cloud.define("register_device", function(request, response) {
	var channelId = request.params.channelId;
	var installationId = request.params.installationId;

	var query = new Parse.Query(Parse.Installation);
	query.equalTo("installationId", installationId);
	query.first().then(function(result) {
		result.set("channels", [channelId]);
		result.save();
	}).then(function() {
		response.success("Successfuly registered the device.");
	}, function(error) {
		response.error("Failed to register the device.");
	});
});
